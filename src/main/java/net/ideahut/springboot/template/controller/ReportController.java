package net.ideahut.springboot.template.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportInput;
import net.ideahut.springboot.report.ReportType;
import net.ideahut.springboot.template.object.ReportData;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.util.FrameworkUtil;
import net.ideahut.springboot.util.StringUtil;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/*
 * Contoh penggunaan ReportHandler
 */
@ComponentScan
@RestController
@RequestMapping("/report")
class ReportController implements InitializingBean {
	
	@Autowired
	private AppProperties appProperties;
	@Autowired
	private ReportHandler reportHandler;
	
	private byte[] template;
	private byte[] imageHeader;
	private byte[] imageDetail;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String path = appProperties.getReportPath();
		Assert.hasLength(path, "AppProperties.reportPath is required");
		path = FrameworkUtil.replacePath(appProperties.getReportPath());
		path = StringUtil.removeEnd(path, "/");
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
		template = resolver.getResource(path + "/sample.jasper").getContentAsByteArray();
		imageHeader = resolver.getResource(path + "/tree1.png").getContentAsByteArray();
		imageDetail = resolver.getResource(path + "/tree2.png").getContentAsByteArray();
	}
	
	@GetMapping
	protected ResponseEntity<StreamingResponseBody> get(
		@RequestParam("name") String name
	) {
		ReportType type = getType(name);
		StreamingResponseBody body = response -> {
			try {
				JasperReport report = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(template));
				ReportInput input = new ReportInput();
				input.setType(type);
				input.setReport(report);
				input.setParameter("MAIN_TITLE", "Contoh Report");
				input.setParameter("SUB_TITLE", type.name());
				input.setParameter("IMAGE_HEADER", new ByteArrayInputStream(imageHeader));
				input.setParameter("IMAGE_DETAIL", new ByteArrayInputStream(imageDetail));
				
				List<ReportData> datasource = new ArrayList<>();
				for (long i = 0; i < 100; i++) {
					ReportData data = new ReportData();
					data.setDescription("Deskripsi - " + System.nanoTime());
					data.setName("Name - " + System.nanoTime());
					data.setNumber(i);
					data.setUuid(UUID.randomUUID().toString());
					datasource.add(data);
				}
				input.setDatasource(datasource);
				
				reportHandler.exportReport(input, response);
			} catch (Exception e) {
				throw FrameworkUtil.exception(e);
			}
		};
		return ResponseEntity.ok().contentType(MediaType.valueOf(type.getContentType())).body(body);
	}
	
	private static ReportType getType(String name) {
		try {
			return ReportType.valueOf(name.trim().toUpperCase());
		} catch (Exception e) {
			return ReportType.PDF;
		}
	}
	
}
