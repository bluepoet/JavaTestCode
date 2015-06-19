package net.bluepoet.test.jsonpath;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static com.jayway.jsonpath.JsonPath.parse;
import static com.jayway.jsonpath.JsonPath.using;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;

public class JsonPathApiTest {
	private static String jsonFile;

	@BeforeClass
	public static void setUp() {
		jsonFile = readFile();
	}

	@Test
	public void exercise_1() throws Exception {
		// Given
		// When
		List<String> authors = JsonPath.read(jsonFile, "$.store.book[*].author");

		// Then
		assertThat(authors).hasSize(4);
		assertThat(authors.get(1)).isEqualTo("Evelyn Waugh");
	}

	@Test
	public void exercise_2() throws Exception {
		// Given
		// When
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonFile);

		// Then
		assertThat(JsonPath.read(document, "$.store.book[1].author")).isEqualTo("Evelyn Waugh");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void exercise_3() throws Exception {
		// Given
		// When
		ReadContext ctx = JsonPath.parse(jsonFile);
		List<String> authors = ctx.read("$.store.book[?(@.isbn)].author");
		List<Map<String, Object>> expensiveBooks = JsonPath.using(Configuration.defaultConfiguration()).parse(jsonFile)
						.read("$.store.book[?(@.price > 10 && @.isbn)]", List.class);

		// Then
		assertThat(authors).hasSize(2);
		assertThat(authors.get(1)).isEqualTo("J. R. R. Tolkien");
		assertThat(expensiveBooks).hasSize(1);
		assertThat(expensiveBooks.get(0).get("author")).isEqualTo("J. R. R. Tolkien");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void exercise_4() throws Exception {
		// Given
		// When
		Filter cheapFilter = filter(where("category").is("fiction").and("price").lte(10D));
		List<Map<String, Object>> cheapBooks = parse(jsonFile).read("$.store.book[?]", cheapFilter);
	
		// Then
		assertThat(cheapBooks).hasSize(1);
		assertThat(cheapBooks.get(0).get("author")).isEqualTo("Herman Melville");
	}
	
	@Test
	public void exercise_5() throws Exception {
		// Given
		// When
		Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
		List<String> pathList = using(conf).parse(jsonFile).read("$..author");
		
		// Then
		assertThat(pathList).containsExactly(
			"$['store']['book'][0]['author']",
			"$['store']['book'][1]['author']",
			"$['store']['book'][2]['author']",
			"$['store']['book'][3]['author']"
		);
	}

	public static String readFile() {
		BufferedReader br = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;
		String result = "";
		String temp = null;

		try {
			File jsonFile = new File(JsonPathApiTest.class.getResource("").getPath() + "books.json");
			fis = new FileInputStream(jsonFile);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);

			while ((temp = br.readLine()) != null) {
				result += temp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}
