package com.zqh.crunch;

import com.zqh.crunch.wc.Tokenizer;
import org.apache.crunch.Emitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@RunWith(MockitoJUnitRunner.class)
public class TokenizerTest {
  @Mock
  private Emitter<String> emitter;

  @Test
  public void testProcess() {
    Tokenizer splitter = new Tokenizer();
    splitter.process("  foo  bar ", emitter);

    verify(emitter).emit("foo");
    verify(emitter).emit("bar");
    verifyNoMoreInteractions(emitter);
  }

}
