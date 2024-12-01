package net.ideahut.springboot.template.support;

import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.RequestReplyFuture;

import net.ideahut.springboot.kafka.KafkaHandler;
import net.ideahut.springboot.kafka.KafkaProperties;
import net.ideahut.springboot.kafka.KafkaSender;
import net.ideahut.springboot.kafka.ReplyKafkaSender;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportInput;
import net.ideahut.springboot.report.ReportResult;
import net.ideahut.springboot.report.ReportType;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/*
 * HANYA UNTUK DEVELOPMENT & TES
 * Daftar unsupported handler jika library tidak include di image (tergantung dari graalvm yang digunakan pada saat build)
 * Kasus:
 * - Report bisa tidak berfungsi karena ada beberapa library yang tidak ada (contoh library awt)
 * - Fungsi kafka bisa dinonaktifkan jika server belum tersedia
 */
public class HandlerSupport {
	
	private HandlerSupport() {}
	
	/*
	 * UNSUPPORTED REPORT HANDLER
	 */
	public static final ReportHandler UNSUPPORTED_REPORT_HANDLER = new ReportHandler() {
		@Override
		public List<ReportType> getReportTypes() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void exportReport(ReportInput input, OutputStream outputStream) throws Exception {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ReportResult createReport(ReportInput input) throws Exception {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ReportResult createReport(ReportInput input, boolean useExportManager) throws Exception {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public JasperPrint createPrint(ReportInput input) throws JRException {
			throw new UnsupportedOperationException();
		}
	};
	
	
	/*
	 * UNSUPPORTED KAFKA HANDLER
	 */
	public static final KafkaHandler UNSUPPORTED_KAFKA_HANDLER = new KafkaHandler() {
		private final KafkaProperties properties = new KafkaProperties();
		
		@SuppressWarnings("rawtypes")
		private final KafkaSender kafkaSender = new KafkaSender() {
			@Override
			public CompletableFuture send(ProducerRecord data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Object key, Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Integer partition, Object key, Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Integer partition, Long timestamp, Object key, Object data) {
				throw new UnsupportedOperationException();
			}
		};
		
		@SuppressWarnings("rawtypes")
		private final ReplyKafkaSender replyKafkaSender = new ReplyKafkaSender() {
			@Override
			public CompletableFuture send(ProducerRecord data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Object key, Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Integer partition, Object key, Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public CompletableFuture send(Integer partition, Long timestamp, Object key, Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public RequestReplyFuture sendAndReceive(ProducerRecord data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public RequestReplyFuture sendAndReceive(ProducerRecord data, Duration replyTimeout) {
				throw new UnsupportedOperationException();
			}
			@Override
			public RequestReplyFuture sendAndReceive(Object data) {
				throw new UnsupportedOperationException();
			}
			@Override
			public RequestReplyFuture sendAndReceive(Object data, Duration replyTimeout) {
				throw new UnsupportedOperationException();
			}
		};

		@Override
		public KafkaProperties getProperties() {
			return properties;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <K, V> KafkaSender<K, V> getSender(String topic) {
			return kafkaSender;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <K, V, R> ReplyKafkaSender<K, V, R> getReplySender(String topic) {
			return replyKafkaSender;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <K, V> KafkaSender<K, V> createSender(String topic) {
			return kafkaSender;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <K, V, R> ReplyKafkaSender<K, V, R> createReplySender(String topic) {
			return replyKafkaSender;
		}
	};

}
