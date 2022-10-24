package com.ailk.wxserver.ci.client.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsClient {

	public static String doPost(String url, String content, String charset)
			throws Exception {
		InputStream in = null;
		ByteArrayOutputStream outStream = null;
		HttpsURLConnection conn = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
					new java.security.SecureRandom());

			URL console = new URL(url);
			conn = (HttpsURLConnection) console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.setDoOutput(true);
			conn.connect();
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.write(content.getBytes(charset));
			// 刷新、关闭
			out.flush();
			close(out);
			in = conn.getInputStream();
			outStream = new ByteArrayOutputStream();
			if (in != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				in.close();
			}
			String ret = new String(outStream.toByteArray(), charset);
			return ret;
		} catch (Exception e) {
			throw e;
		} finally {
			close(in);
			close(outStream);
			try {
				conn.disconnect();
			} catch (Exception e) {
			}
		}

	}

	public static String doGet(String url, String charset) throws Exception {
		InputStream in = null;
		ByteArrayOutputStream outStream = null;
		HttpsURLConnection conn = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
					new java.security.SecureRandom());
			URL console = new URL(url);
			conn = (HttpsURLConnection) console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.connect();
			in = conn.getInputStream();
			outStream = new ByteArrayOutputStream();
			if (in != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
			}
			String str_return = new String(outStream.toByteArray(), charset);
			return str_return;
		} catch (Exception e) {
			throw e;
		} finally {
			close(in);
			close(outStream);
			try {
				conn.disconnect();
			} catch (Exception e) {
			}

		}
	}

	public static String post(String url, String content, String charset) {
		String rsp = "";
		HttpURLConnection conn = null;
		OutputStreamWriter writer = null;
		BufferedReader reader = null;
		try {
			URL u = new URL(url);
			if (url.toLowerCase().startsWith("https://")) {
				// setHttpsConnection();
			}
			long startTime = System.nanoTime();
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length",
					String.valueOf(content.length()));
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "close");
			conn.setConnectTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			writer = new OutputStreamWriter(conn.getOutputStream(), charset);
			writer.write(content);
			writer.flush();
			int code = conn.getResponseCode();
			if (code != HttpURLConnection.HTTP_OK) {

			}
			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), charset));
			StringBuffer sb = new StringBuffer();
			String temp;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp).append('\n');
			}
			rsp = sb.toString();
			long endTime = System.nanoTime();

		} catch (Exception e) {
			rsp = "";
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != conn) {
				conn.disconnect();
			}

		}
		return rsp;
	}

	private static void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
		}

	}

	static class TrustAnyTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
}
