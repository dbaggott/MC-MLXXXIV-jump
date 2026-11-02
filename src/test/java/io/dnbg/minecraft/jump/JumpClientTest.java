package io.dnbg.minecraft.jump;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class JumpClientTest {
	@Test
	void modIdMatchesFabricModJson() throws IOException {
		try (InputStream in = JumpClientTest.class.getResourceAsStream("/fabric.mod.json")) {
			assertNotNull(in, "fabric.mod.json is not on the test classpath");
			try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
				JsonObject metadata = JsonParser.parseReader(reader).getAsJsonObject();
				assertEquals(metadata.get("id").getAsString(), JumpClient.MOD_ID);
			}
		}
	}
}
