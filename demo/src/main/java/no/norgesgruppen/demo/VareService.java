package no.norgesgruppen.demo;

import java.util.Arrays;
import java.util.List;

import no.norgesgruppen.demo.annotations.Cacheable;

public class VareService {

	@Cacheable
	public List<String> search(String query, int limit) {
		return Arrays.asList("foo", "bar", "baz", "cat");
	}

}
