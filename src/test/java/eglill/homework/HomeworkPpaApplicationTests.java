package eglill.homework;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeworkPpaApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    void getSumReadsInputAndProducesOutput() throws Exception {
        String url = "/sum?number1=3&number2=4";
        String response = this.mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(response, is("{\"number1\":3,\"number2\":4,\"sum\":7}"));
    }

    @Test
    void getSumValidatesInput() throws Exception {
        this.mockMvc.perform(get("/sum?number1=-3&number2=4")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/sum?number1=3&number2=400")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/sum?number1=-3&number2=400")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/sum?number2=4")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/sum?number1=3")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/sum")).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @DirtiesContext
    void getAllReadsInputAndProducesCorrectOutput() throws Exception {
        this.mockMvc.perform(get("/sum?number1=3&number2=4"));
        this.mockMvc.perform(get("/sum?number1=3&number2=6"));
        this.mockMvc.perform(get("/sum?number1=3&number2=5"));
        String response = this.mockMvc.perform(get("/all?order=INCREASING")).
                andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(response, is("[{\"number1\":3,\"number2\":4,\"sum\":7},{\"number1\":3,\"number2\":5,\"sum\":8},{\"number1\":3,\"number2\":6,\"sum\":9}]"));
        response = this.mockMvc.perform(get("/all?order=DECREASING")).
                andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(response, is("[{\"number1\":3,\"number2\":6,\"sum\":9},{\"number1\":3,\"number2\":5,\"sum\":8},{\"number1\":3,\"number2\":4,\"sum\":7}]"));
        response = this.mockMvc.perform(get("/all?number=6&order=DECREASING")).
                andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(response, is("[{\"number1\":3,\"number2\":6,\"sum\":9}]"));
    }

    @Test
    void getAllValidatesInput() throws Exception {
        this.mockMvc.perform(get("/all?number=400&order=DECREASING")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/all?number=-4&order=DECREASING")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/all?order=SomethingElse")).andExpect(status().isBadRequest()).andReturn();
        this.mockMvc.perform(get("/all")).andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    @DirtiesContext
    void threadTest() throws Exception {
        int count = 1000;
        int numberOfThreads = 4;
        for (int i = 0; i < count; i++) {
            getSumUsingThreads(numberOfThreads);
        }
        MvcResult result = this.mockMvc.perform(get("/all?order=INCREASING")).andExpect(status().isOk()).andReturn();
        int countOfGetRequestsMade = result.getResponse().getContentAsString().split(",\\{").length;
        assertThat(countOfGetRequestsMade, is(count * numberOfThreads));
    }

    void getSumUsingThreads(int numberOfThreads) throws Exception {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            String url = String.format("/sum?number1=%d&number2=%d", i, i);
            Thread thread = new Thread(() -> {
                try {
                    this.mockMvc.perform(get(url)).andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
