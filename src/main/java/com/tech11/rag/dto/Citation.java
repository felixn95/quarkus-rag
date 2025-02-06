package com.tech11.rag.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Citation {
	// An identifier for the citation, if required
	private String id;

	// The citation text or title
	private String text;

	// Optionally, a URL or link associated with the citation
	private String url;
}
