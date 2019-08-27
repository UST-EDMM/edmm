package io.github.edmm.docker;

import io.github.edmm.utils.Consts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DockerfileBuilderTest {

    private DockerfileBuilder builder;

    @Before
    public void init() {
        builder = new DockerfileBuilder();
        builder.from("ubuntu")
                .env("foo", "bar")
                .run("echo \"foo\"")
                .run("echo \"bar\"")
                .copy("./config.txt", "/tmp")
                .add("./config.txt", "/tmp")
                .expose(80)
                .volume("/tmp")
                .workdir("/tmp")
                .entrypoint("sh", "echo")
                .cmd("hello")
                .build();
    }

    @Test
    public void testWithoutCompression() {
        String expected = "FROM ubuntu" + Consts.NL +
                "ENV foo=bar" + Consts.NL +
                "RUN echo \"foo\"" + Consts.NL +
                "RUN echo \"bar\"" + Consts.NL +
                "COPY ./config.txt /tmp" + Consts.NL +
                "ADD ./config.txt /tmp" + Consts.NL +
                "EXPOSE 80" + Consts.NL +
                "VOLUME /tmp" + Consts.NL +
                "WORKDIR /tmp" + Consts.NL +
                "ENTRYPOINT [\"sh\", \"echo\"]" + Consts.NL +
                "CMD [\"hello\"]";
        Assert.assertEquals(expected.trim(), builder.build().trim());
    }

    @Test
    public void testWithCompression() {
        builder.compress();
        String expected = "FROM ubuntu" + Consts.NL +
                "ENV foo=bar" + Consts.NL +
                "RUN echo \"foo\" && \\" + Consts.NL +
                "    echo \"bar\"" + Consts.NL +
                "COPY ./config.txt /tmp" + Consts.NL +
                "ADD ./config.txt /tmp" + Consts.NL +
                "EXPOSE 80" + Consts.NL +
                "VOLUME /tmp" + Consts.NL +
                "WORKDIR /tmp" + Consts.NL +
                "ENTRYPOINT [\"sh\", \"echo\"]" + Consts.NL +
                "CMD [\"hello\"]";
        Assert.assertEquals(expected.trim(), builder.build().trim());
    }
}
