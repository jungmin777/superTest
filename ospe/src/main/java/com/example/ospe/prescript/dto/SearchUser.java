package com.example.ospe.prescript.dto;

import java.time.LocalDate;

import org.hibernate.internal.build.AllowSysOut;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllowSysOut
public class SearchUser {
	private Long id;
	private String username;
	private String name;
	private LocalDate birthDate;
}
