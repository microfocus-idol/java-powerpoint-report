/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.idol.web;

import com.hp.autonomy.frontend.find.core.web.RequestMapper;
import com.hp.autonomy.frontend.find.core.web.RequestMapperTest;
import com.hp.autonomy.searchcomponents.idol.requests.IdolRequestBuilderConfiguration;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequest;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRequestBuilder;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictions;
import com.hp.autonomy.searchcomponents.idol.search.IdolQueryRestrictionsBuilder;
import com.hp.autonomy.types.requests.idol.actions.query.params.SummaryParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.apache.commons.io.IOUtils;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@SpringBootTest(classes = IdolRequestBuilderConfiguration.class)
public class IdolRequestMapperTest extends RequestMapperTest<IdolQueryRequest, IdolQueryRestrictions, String> {
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private IdolQueryRestrictionsBuilder queryRestrictionsBuilder;
    @Autowired
    private IdolQueryRequestBuilder queryRequestBuilder;

    @Override
    protected RequestMapper<IdolQueryRequest> constructRequestMapper() {
        return new IdolRequestMapper(queryRestrictionsBuilder, queryRequestBuilder);
    }

    @Override
    protected String completeJsonObject() throws IOException {
        return IOUtils.toString(IdolRequestMapperTest.class.getResourceAsStream("/com/hp/autonomy/frontend/find/idol/web/search-request.json"));
    }

    @Override
    protected String minimalJsonObject() throws IOException {
        return IOUtils.toString(IdolRequestMapperTest.class.getResourceAsStream("/com/hp/autonomy/frontend/find/idol/web/search-request-minimal.json"));
    }

    @Override
    protected void validateDatabases(final List<String> databases) {
        assertThat(databases, hasItem(is("ClassicalLiterature")));
        assertThat(databases, hasItem(is("EpicLiterature")));
    }

    @Override
    protected void validate(final IdolQueryRequest queryRequest) {
        assertThat(queryRequest.getSummary(), is(SummaryParam.Off));
        assertThat(queryRequest.getSort(), is(SortParam.DocumentCount));
    }

    @Override
    protected void validateMinimal(final IdolQueryRequest queryRequest) {
        assertThat(queryRequest.getSummary(), is(SummaryParam.Off));
        assertNull(queryRequest.getSort());
    }
}
