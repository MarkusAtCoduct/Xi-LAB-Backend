package com.codeleap.xilab.api.process;

import com.codeleap.xilab.api.controllers.MethodController;
import com.codeleap.xilab.api.security.WebSecurityConfig;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Map;

@ContextConfiguration(classes = { WebSecurityConfig.class })
@Service
public class MockProcessor {

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(new MethodController()).build();
	}

	public MockHttpServletResponse performMockPost(String apiUri, MultiValueMap<String, String> params, String jsonBody,
			HttpHeaders httpHeaders, ResultMatcher resultMatcher) throws Exception {
		return performMockPost(apiUri, params, jsonBody, httpHeaders, null, resultMatcher);
	}

	public MockHttpServletResponse performMockPut(String apiUri, MultiValueMap<String, String> params, String jsonBody,
			HttpHeaders httpHeaders, ResultMatcher resultMatcher) throws Exception {
		return performMockPut(apiUri, params, jsonBody, httpHeaders, null, resultMatcher);
	}

	public MockHttpServletResponse performMockPatch(String apiUri, MultiValueMap<String, String> params,
			String jsonBody, HttpHeaders httpHeaders, ResultMatcher resultMatcher) throws Exception {
		return performMockPatch(apiUri, params, jsonBody, httpHeaders, null, resultMatcher);
	}

	public MockHttpServletResponse performMockPut(String apiUri, MultiValueMap<String, String> params, String jsonBody,
			HttpHeaders httpHeaders, Map<String, Object> requestAttrs, ResultMatcher resultMatcher) throws Exception {
		if (params == null) {
			params = new LinkedMultiValueMap<>();
		}

		MockHttpServletRequestBuilder putBuilder = MockMvcRequestBuilders.put(apiUri)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).servletPath(apiUri)
				.accept(MediaType.APPLICATION_JSON).params(params)
				.headers(httpHeaders != null ? httpHeaders : new HttpHeaders())
				.content(!StringUtils.isEmpty(jsonBody) ? jsonBody : "");

		if (requestAttrs != null) {
			for (Map.Entry<String, Object> entry : requestAttrs.entrySet()) {
				putBuilder.requestAttr(entry.getKey(), entry.getValue());
			}
		}

		return mockMvc.perform(putBuilder).andExpect(resultMatcher).andDo(MockMvcResultHandlers.print()).andReturn()
				.getResponse();
	}

	public MockHttpServletResponse performMockPatch(String apiUri, MultiValueMap<String, String> params,
			String jsonBody, HttpHeaders httpHeaders, Map<String, Object> requestAttrs, ResultMatcher resultMatcher)
			throws Exception {
		if (params == null) {
			params = new LinkedMultiValueMap<>();
		}

		MockHttpServletRequestBuilder patchBuilder = MockMvcRequestBuilders.patch(apiUri)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).servletPath(apiUri)
				.accept(MediaType.APPLICATION_JSON_VALUE).params(params)
				.headers(httpHeaders != null ? httpHeaders : new HttpHeaders())
				.content(!StringUtils.isEmpty(jsonBody) ? jsonBody : "");

		if (requestAttrs != null) {
			for (Map.Entry<String, Object> entry : requestAttrs.entrySet()) {
				patchBuilder.requestAttr(entry.getKey(), entry.getValue());
			}
		}

		return mockMvc.perform(patchBuilder).andExpect(resultMatcher).andDo(MockMvcResultHandlers.print()).andReturn()
				.getResponse();
	}

	public MockHttpServletResponse performMockPost(String apiUri, MultiValueMap<String, String> params, String jsonBody,
			HttpHeaders httpHeaders, Map<String, Object> requestAttrs, ResultMatcher resultMatcher) throws Exception {
		if (params == null) {
			params = new LinkedMultiValueMap<>();
		}

		MockHttpServletRequestBuilder postBuilder = MockMvcRequestBuilders.post(apiUri)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).servletPath(apiUri)
				.accept(MediaType.APPLICATION_JSON_VALUE).params(params)
				.headers(httpHeaders != null ? httpHeaders : new HttpHeaders())
				.content(!StringUtils.isEmpty(jsonBody) ? jsonBody : "");

		if (requestAttrs != null) {
			for (Map.Entry<String, Object> entry : requestAttrs.entrySet()) {
				postBuilder.requestAttr(entry.getKey(), entry.getValue());
			}
		}

		return mockMvc.perform(postBuilder).andExpect(resultMatcher).andDo(MockMvcResultHandlers.print()).andReturn()
				.getResponse();
	}

	public MockHttpServletResponse performMockGet(String apiUri, MultiValueMap<String, String> params,
			HttpHeaders httpHeaders, ResultMatcher resultMatcher) throws Exception {
		return performMockGet(apiUri, params, httpHeaders, null, resultMatcher);
	}

	public MockHttpServletResponse performMockGet(String apiUri, MultiValueMap<String, String> params,
			HttpHeaders httpHeaders, Map<String, Object> requestAttrs, ResultMatcher resultMatcher) throws Exception {
		if (params == null) {
			params = new LinkedMultiValueMap<>();
		}
		MockHttpServletRequestBuilder getBuilder = MockMvcRequestBuilders.get(apiUri)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).params(params).servletPath(apiUri)
				.accept(MediaType.APPLICATION_JSON_VALUE)
				.headers(httpHeaders != null ? httpHeaders : new HttpHeaders());

		if (requestAttrs != null) {
			for (Map.Entry<String, Object> entry : requestAttrs.entrySet()) {
				getBuilder.requestAttr(entry.getKey(), entry.getValue());
			}
		}

		ResultActions result = mockMvc.perform(getBuilder);
		return result.andExpect(resultMatcher).andDo(MockMvcResultHandlers.print()).andReturn().getResponse();
	}

}