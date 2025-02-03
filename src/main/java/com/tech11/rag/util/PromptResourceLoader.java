package com.tech11.rag.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PromptResourceLoader {
	public static String loadResource(String path) {
		try (Scanner scanner = new Scanner(PromptResourceLoader.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8.name())) {
			return scanner.useDelimiter("\\A").next();
		} catch (Exception e) {
			throw new RuntimeException("Unable to load resource: " + path, e);
		}
	}
}
