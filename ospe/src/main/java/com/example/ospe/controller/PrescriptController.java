package com.example.ospe.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ospe.prescript.dao.PrescriptRepository;
import com.example.ospe.prescript.dto.Prescript;
import com.example.ospe.prescript.dto.SearchUser;
import com.example.ospe.reservation.dto.Headerlogin;
import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;
import com.example.ospe.user.service.DoctorService;
import com.example.ospe.user.service.UserService;



@Controller
public class PrescriptController {
	
	@Autowired
	private PrescriptRepository pr;
	
	@Autowired
	private UserService us;
	@Autowired
	private UserRepository ur;
	
	@Autowired
	private DoctorService ds;
	@Autowired
	private DoctorRepository dr;
	
	@Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute //모든 매핑에 추가할 코드
    public void addAttributes(Model model, Principal principal) {
        keep.headerlogin(model, principal); //로그인 유지 
    }
	
	
	@GetMapping("/history/patient")
	public String historyPrescriptPatient(
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(required = false) String searchselect,
	    @RequestParam(required = false) String searchinput,
	    Model model, 
	    Principal principal
	) {
	    // 로그인 여부 확인
	    if (principal == null) {
	        return "redirect:/login"; // 비로그인 사용자는 로그인 페이지로 리다이렉트
	    }

	    // 로그인된 사용자 정보 추가
	    model.addAttribute("loggedIn", true);
	    model.addAttribute("username", principal.getName());

	    // 사용자 정보 가져오기
	    List<User> users = ur.findByUsername(principal.getName());
	    if (users.isEmpty()) {
	        return "redirect:/login"; // 사용자 정보가 없으면 로그인 페이지로 리다이렉트
	    }

	    User user = users.get(0);
	    List<Prescript> prescripts = pr.findByPatientName(user.getName());

	    // 검색 조건 처리
	    if (searchselect != null && !searchselect.isEmpty() && searchinput != null && !searchinput.isEmpty()) {
	        switch (searchselect) {
	            case "환자명":
	                prescripts = prescripts.stream()
	                    .filter(p -> p.getPatientName() != null && p.getPatientName().contains(searchinput))
	                    .collect(Collectors.toList());
	                break;
	            case "약종류":
	                prescripts = prescripts.stream()
	                    .filter(p -> p.getMedications() != null && p.getMedications().contains(searchinput))
	                    .collect(Collectors.toList());
	                break;
	            case "의사명":
	                prescripts = prescripts.stream()
	                    .filter(p -> p.getDoctorName() != null && p.getDoctorName().contains(searchinput))
	                    .collect(Collectors.toList());
	                break;
	            default:
	                break;
	        }
	    }

	    // 약물 처리
	    for (Prescript prescript : prescripts) {
	        String medications = prescript.getMedications();
	        if (medications == null) {
	            medications = ""; // 기본값으로 빈 문자열 설정
	        } else {
	            medications = medications.replace("%&%", ",");
	            if (medications.endsWith(",")) {
	                medications = medications.substring(0, medications.length() - 1);
	            }
	        }
	        prescript.setMedications(medications);
	    }

	    // 페이지네이션 처리
	    int pageSize = 10;
	    int totalItems = prescripts.size();
	    int totalPages = (int) Math.ceil((double) totalItems / pageSize);
	    int startIndex = (page - 1) * pageSize;
	    int endIndex = Math.min(startIndex + pageSize, totalItems);

	    List<Prescript> paginatedPrescripts = prescripts.subList(startIndex, endIndex);

	    // 페이지네이션 범위 설정
	    int paginationSize = 10;
	    int currentRangeStart = ((page - 1) / paginationSize) * paginationSize + 1;
	    int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalPages);

	    // 모델에 데이터 추가
	    model.addAttribute("PatientPrescript", paginatedPrescripts);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("startPage", currentRangeStart);
	    model.addAttribute("endPage", currentRangeEnd);
	    model.addAttribute("searchselect", searchselect);
	    model.addAttribute("searchinput", searchinput);

	    return "prescript/history_pati"; // Thymeleaf 뷰 반환
	}



	
	   @GetMapping("/history/doctor")
	   public String historyPrescriptDoctor(
	       @RequestParam(defaultValue = "1") int page,
	       @RequestParam(value = "searchselect", required = false) String searchselect,
	       @RequestParam(value = "searchinput", required = false) String searchinput,
	       Model model, Principal principal
	   ) {
	       // 로그인 여부 확인
	       if (principal == null) {
	           return "redirect:/login"; // 비로그인 사용자는 로그인 페이지로 리다이렉트
	       }
	 
	       // 로그인된 사용자 정보 추가
	       model.addAttribute("loggedIn", true);
	       model.addAttribute("username", principal.getName());
	       
	       // 의사 정보 가져오기
	       List<Doctor> doctors = dr.findByUsername(principal.getName());
	       if (doctors.isEmpty()) {
	           return "redirect:/login"; // 의사가 존재하지 않을 경우 로그인 페이지로 리다이렉트
	       }

	       Doctor doctor = doctors.get(0);
	       model.addAttribute("doctorname", doctor.getName());
	       
	       List<Prescript> prescripts = pr.findByDepartmentOrderByPrescriptNumDesc(doctor.getSpecialty());
	       for (Prescript prescript : prescripts) {
	           String medications = prescript.getMedications();
	           if (medications == null) {
	               medications = ""; // 기본값으로 빈 문자열 설정
	           } else {
	               medications = medications.replace("%&%", ",");
	               if (medications.endsWith(",")) {
	                   medications = medications.substring(0, medications.length() - 1);
	               }
	           }
	           prescript.setMedications(medications);
	       }

	       // 검색 조건 처리
	       if (searchselect != null && !searchselect.isEmpty() && searchinput != null && !searchinput.isEmpty()) {
	           switch (searchselect) {
	               case "환자명":
	                   prescripts = prescripts.stream()
	                       .filter(p -> p.getPatientName() != null && p.getPatientName().contains(searchinput))
	                       .collect(Collectors.toList());
	                   break;
	               case "약종류":
	                   prescripts = prescripts.stream()
	                       .filter(p -> p.getMedications() != null && p.getMedications().contains(searchinput))
	                       .collect(Collectors.toList());
	                   break;
	               default:
	                   break;
	           }
	       }

	       // 페이지네이션 처리
	       int pageSize = 10;
	       int totalItems = prescripts.size();
	       int totalPages = (int) Math.ceil((double) totalItems / pageSize);
	       int startIndex = (page - 1) * pageSize;
	       int endIndex = Math.min(startIndex + pageSize, totalItems);

	       List<Prescript> paginatedPrescripts = prescripts.subList(startIndex, endIndex);

	       int paginationSize = 10;
	       int currentRangeStart = ((page - 1) / paginationSize) * paginationSize + 1;
	       int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalPages);

	       // 모델에 데이터 추가
	       model.addAttribute("DoctorPrescript", paginatedPrescripts);
	       model.addAttribute("currentPage", page);
	       model.addAttribute("totalPages", totalPages);
	       model.addAttribute("startPage", currentRangeStart);
	       model.addAttribute("endPage", currentRangeEnd);
	       model.addAttribute("searchselect", searchselect);
	       model.addAttribute("searchinput", searchinput);

	       return "prescript/history";
	   }


	
	@GetMapping("/history")
	   public String InHistory( 
			   Model model,Principal principal) {
	         //      principal.getName() 은 아이디 가져옴
	      if (principal != null) {
	         // 사용자가 로그인한 경우
	         model.addAttribute("loggedIn", true);
	         model.addAttribute("username", principal.getName());
	      } else {
	         // 사용자가 로그인하지 않은 경우
	         model.addAttribute("loggedIn", false);
	      }
	         User user =  us.getUserByUsername(principal.getName()); // 로그인된 아이디로 user 에 있는지 확인
	         Doctor doctor = ds.getDoctorByUsername(principal.getName()); // 로그인된 아이디로 doctor 에 있는지 확인 
	         if(user != null) { // 만약 로그인된 아이디가 user 에 있다면
	            return "redirect:/history/patient"; // "/history/patient" 페이지로 리다이렉트
	         }else if(doctor != null) { // 만약 로그인된 아이디가 doctor 에 있다면
	            return "redirect:/history/doctor"; // "/history/doctor" 페이지로 리다이렉트
	         }else { // 둘 다 아니라면
	            return "redirect:/login"; // "/login" 페이지로 리다이렉트
	      }
	   } 
	
	
	// 로그인유저의 처방전 목록
	@GetMapping("/preList")
	public String prescriptList(Model model, Principal principal,
			@RequestParam(defaultValue="1", name="page") int page,
			 @RequestParam(required = false, name = "patientSearch") String patientSearch) {
		int pageSize = 10;
		int paginationSize = 10;
//		principal.getName() 은 아이디 가져옴
		if (principal != null) {
			// 사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		
		// |여기부터                         여기까지|
		// 는 doctor 에 로그인한 유저가 있을 경우 처방전 작성 버튼을 보이게 하기 위해서
		// 대충 넣는 model 입니다
		if(!dr.findByUsername(principal.getName()).isEmpty()) model.addAttribute("d","d");
		
		// if 내에서 변수를 만들면 밖에서 사용 못하기 때문에 밖에 만듬
		long a = 0;
		String username = principal.getName(); // 로그인할때의 아이디
		List<Prescript> preList = null;
		if(username != null) {
			User user = us.getUserByUsername(username); // 로그인된 아이디로 user 에 있는지 찾아봄
			Doctor doctor = ds.getDoctorByUsername(username); // 로그인된 아이디로 doctor 에 있는지 찾아봄
			if (user == null) { // 의사로 로그인한 경우
	            String n = doctor.getName();

	            if (patientSearch != null && !patientSearch.trim().isEmpty()) { // 검색하였을 때
	                // 검색어로 필터링 (의사가 로그인한 경우)
	                preList = pr.findByDoctorNameAndPatientNameContaining(n, patientSearch);
	            } else {
	                // 의사가 로그인한 경우 본인의 모든 처방전을 가져옴
	                preList = pr.findByDoctorName(n);
	            }

	        } else { // 일반 사용자가 로그인한 경우
	            a = user.getId();
            	preList = pr.findByPatientNum(a); // 위에서 받아온 정보
	        }
		} else { // 유저네임이 없다 = 로그인 안함
			return "redirect:/login";
		}
		preList.sort(Comparator.comparingLong(Prescript::getPrescriptNum).reversed());
		// 찾은 처방전리스트 모델
		int totalPres = preList.size();
		int totalPages = (int) Math.ceil((double) totalPres / pageSize);
		int startIndex = (page - 1) * pageSize;
	    int endIndex = Math.min(startIndex + pageSize, totalPres);
	    int currentRangeStart = ((page - 1) / paginationSize) * paginationSize + 1;
	    int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalPages);
	    List<Prescript> sortPreList = preList.subList(startIndex, endIndex);
	    
		model.addAttribute("prescripts", sortPreList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", currentRangeStart);
		model.addAttribute("endPage", currentRangeEnd);
		return "prescript/prescriptList";
	}
	
	
//	처방전을 선택했을 때 상세조회
	@GetMapping("/prescriptDetail/{preNum}")
	public String prescriptDetail(Model model, @PathVariable int preNum, Principal principal) {
		if (principal != null) {
	 		//	사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		
		// 의사로 로그인했을 경우 처방전 수정 버튼을 보이게 하기 위해서
		// 아이디는 유니크 =>  
		List<Doctor> d = dr.findByUsername(principal.getName());
		Doctor ddd = null;
		if(!d.isEmpty()) {ddd = d.get(0);} // 리스트가 비어있지 않다 = 의사로 로그인했다
		if(!d.isEmpty()) {
			model.addAttribute("d", ddd);
		}
		
		// 처방전의 PrimaryKey 로 prescript 1개 가져오기
		Prescript selected = pr.findByPrescriptNum(preNum);
		model.addAttribute("prescript", selected);
		return "/prescript/prescriptDetail";
	}
	
	
//	의사가 처방전을 작성버튼을 눌렀을 경우 환자 검색 & 선택하게
	@GetMapping("/selectPatient")
	public String selectPatient(Model model, Principal principal){
		if (principal != null) {
	 		//	사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		
		// 단순한 화면 이동의 기능만 존재
		return "prescript/selectPatient";
	}
	
	
//	처방전 작성 페이지
	@GetMapping("/insertPrescriptView/{id}") // 의사만 입력 가능, 접근 가능
	public String insertPrescriptView(
				Model model, Principal principal,
				@PathVariable long id
			) {
		if (principal != null) {
			// 사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		
		// dr.findByUserName() 은 List 를 반환하지만 아이디는 중복 불가능이라 리스트에 하나만 들어있을 것이기에
		// 바로 .get(0) 해서 첫번째 Doctor 를 가져와줌
		Doctor d = dr.findByUsername(principal.getName()).get(0);
		// 의사관련 정보들을 입력하기 위해 모델
		model.addAttribute("doctor",d);
		
		// 이미 만들어져있는 메소드 findById 를 사용하기 위해 Optional 을 사용
		Optional<User> u = ur.findById(id);
		// Optional 에서 가져오는 방법은 .orElse()를 사용하는 방법
		User user = u.orElse(null);
		model.addAttribute("patient", user);

		return "prescript/insertPrescript";
	}
	
//	처방전 만든거 입력
	@GetMapping("/insertPrescript")
	public String insertPrescript(@ModelAttribute Prescript prescript, Principal principal,
			Model model, @RequestParam("preDate") String today) {
		if (principal != null) {
	 		//	사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		
		// string 타입으로 받아온 today 변수를 Date 형태로 바꿔주기 위해
		// 내장된 기능 사용
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// sdf.parse(today) : 강제형변환 => 강제기 때문에 try ~ catch 를 사용함
			prescript.setPrescriptDate(sdf.parse(today));
		} catch (Exception e) {}
		
		// Prescript 테이블에 저장
		if(prescript.getMedications().endsWith("%&%")) { // 받아올 때 왼쪽의 문자열로 이어붙여서 가져왔기 때문에
			// 마지막에 붙은 특수문자열을 삭제하는 법
			String str = prescript.getMedications().substring(0, prescript.getMedications().length()-3 );
			//           처방전 =   약1%&%약2%&%약3%&% = 섭스트링은 문자열에서 원하는 무언가를 없애는 방법
			prescript.setMedications(str);
			// 없앤 문자열 약1%&%약2%&%약3 을 set 해줌
		}
		
		// 완성된 prescript 를 저장해줌
		pr.save(prescript);
		return "redirect:/preList";
	}
	
//	처방전 수정 페이지로 이동
	@GetMapping("/modifyPrescriptView/{preNum}")
	public String modifyPrescriptView(
			Model model, @PathVariable int preNum, Principal principal
	) {
		if (principal != null) {
	 		//	사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		
		// 이미 작성되어있는 처방전을 가져와줌
		Prescript selected = pr.findByPrescriptNum(preNum);
		model.addAttribute("prescript", selected);
		
		// 로그인 유저의 이름을 가져와서 처방전을 작성한 의사의 이름과 비교
		Doctor d = dr.findByUsername(principal.getName()).get(0);
		if(!selected.getDoctorName().equals(d.getName())) {
			// 이름이 동일하지 않다면 다시 리스트로 보냄 = 수정 불가능
			return "redirect:/preList";
			// 작성한 의사 본인만 수정가능하게 만듬 (아마도)
		}
		
		return "prescript/modifyPrescript";
	}
	
//	처방전 수정 저장 (단, 약 관련만 수정가능)
	@GetMapping("/modifyPrescript")
	public String modifyPrescript(
		@ModelAttribute Prescript pre, Principal principal, Model model, @RequestParam("preDate") String today
	) {
		if (principal != null) {
	 		//	사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		
		// 똑같은거 위에 있으니 생략
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			pre.setPrescriptDate(sdf.parse(today));
		} catch (Exception e) {}
		// Prescript 테이블에 저장

		// 여기도 생략
		if(pre.getMedications().endsWith("%&%")) {
			String str = pre.getMedications().substring(0, pre.getMedications().length()-3 );
			pre.setMedications(str);
		}
		
		// save()를 사용하지만 이미 존재하기 때문에 insert 가 아닌 update 가 됨
		pr.save(pre);
		return "redirect:/prescriptDetail/"+pre.getPrescriptNum();
		// redirect 는 mapping 된 주소로 이동하는데 이때 위의 번호를 넘기면
		// 최종적으로
		// 수정 버튼 클릭 -> 이 메소드로 들어옴 -> 수정된 내용을 확인할 수 있게 다시 상세조회로 감
	}
	
	
//	selecPatient 에서 환자 이름으로 검색할 때 사용 (ajax 기법)
	@GetMapping("/getPatient")
	@ResponseBody
	public ArrayList<SearchUser> getPatient(@RequestParam("username") String name){
		// ajax 기법인데 화면상에서 페이지의 새로고침 없이 DB 에 갔다오는 방법
		// 자세히 알고싶으면 물어보세요
		// 자바스크립트에서 username 이라는 이름으로 데이터를 보내기 때문에 
		// @RequestParam 을 사용해서 받아올 수 있음
//		System.out.println("name : "+name);
//		System.out.println(ur.findByNameLike("%"+name+"%"));
		// findByNameLike
		// select * from table where name like ...
		// 에서 like 를 사용하는 방법은
		// 1. _정민_ => 정민 단어의 앞, 뒤 한글자씩 존재
		// 2. %정민% => 정민 단어가 포함되어잇기만 하면 됨
		// 3. %정민_ => 0개 이상의 단어 + 정민 + 1글자
//		List<SearchUser> su = new List<SearchUser>();
		List<User> users = ur.findByNameLike("%"+name+"%");
		ArrayList<SearchUser> sus = new ArrayList<SearchUser>();
		for(int i = 0; i < users.size(); i++) {
			SearchUser su = new SearchUser();
			su.setId(users.get(i).getId());
			su.setUsername(users.get(i).getUsername());
			su.setName(users.get(i).getName());
			su.setBirthDate(users.get(i).getBirthDate());
			sus.add(su);
		}
//		System.out.println(ur.findByNameLike("%"+name+"%"));
//		return ur.findByNameLike("%"+name+"%");
		return sus;
		
		
//		if(name.trim() == null) {
//			return null;
//		}else {
			
//		}
	}
	
}
