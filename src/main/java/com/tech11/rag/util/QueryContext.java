package com.tech11.rag.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryContext {
	private  String historySummary;
	private  String queryForVectorSearch;
}
