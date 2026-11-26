package com.example.crudwithvaadin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller returning application metadata and student identification.
 */
@RestController
public class InfoRestController {

	private final String studentId;
	private final String applicationName;
	private final String version;

	public InfoRestController(
			@Value("${app.student-id:79841}") String studentId,
			@Value("${spring.application.name:First-Java-CRUD-REST}") String applicationName,
			@Value("${app.version:1.0.0}") String version) {
		this.studentId = studentId;
		this.applicationName = applicationName;
		this.version = version;
	}

	@GetMapping("/api/info")
	public AppInfo getInfo() {
		return new AppInfo(studentId, applicationName, version);
	}

	public record AppInfo(String studentId, String applicationName, String version) {
	}
}
