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
                .run("echo \"foo\"")
                .run("echo \"bar\"")
                .env("foo", "bar")
                .copy("./test.txt", "/tmp")
                .workdir("/tmp")
                .add("./test.txt", "/tmp")
                .workdir("/opt")
                .expose(80)
                .volume("/tmp")
                .entrypoint("sh", "echo")
                .cmd("hello")
                .env("baz", "qux");
    }

    @Test
    public void testWithoutCompression() {
        String expected = "FROM ubuntu" + Consts.NL +
                "ENV foo=bar" + Consts.NL +
                "ENV baz=qux" + Consts.NL +
                "RUN echo \"foo\"" + Consts.NL +
                "RUN echo \"bar\"" + Consts.NL +
                "COPY ./test.txt /tmp" + Consts.NL +
                "WORKDIR /tmp" + Consts.NL +
                "ADD ./test.txt /tmp" + Consts.NL +
                "WORKDIR /opt" + Consts.NL +
                "EXPOSE 80" + Consts.NL +
                "VOLUME /tmp" + Consts.NL +
                "ENTRYPOINT [\"sh\", \"echo\"]" + Consts.NL +
                "CMD [\"hello\"]";
        Assert.assertEquals(expected.trim(), builder.build().trim());
    }

    @Test
    public void testWithCompression() {
        builder.compress();
        String expected = "FROM ubuntu" + Consts.NL +
                "ENV foo=bar" + Consts.NL +
                "ENV baz=qux" + Consts.NL +
                "RUN echo \"foo\" && \\" + Consts.NL +
                "    echo \"bar\"" + Consts.NL +
                "COPY ./test.txt /tmp" + Consts.NL +
                "WORKDIR /tmp" + Consts.NL +
                "ADD ./test.txt /tmp" + Consts.NL +
                "WORKDIR /opt" + Consts.NL +
                "EXPOSE 80" + Consts.NL +
                "VOLUME /tmp" + Consts.NL +
                "ENTRYPOINT [\"sh\", \"echo\"]" + Consts.NL +
                "CMD [\"hello\"]";
        Assert.assertEquals(expected.trim(), builder.build().trim());
    }
}
