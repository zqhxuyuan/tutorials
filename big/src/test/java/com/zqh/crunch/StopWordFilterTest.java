package com.zqh.crunch;

import com.zqh.crunch.wc.StopWordFilter;
import org.apache.crunch.FilterFn;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class StopWordFilterTest {

  @Test
  public void testFilter() {
    FilterFn<String> filter = new StopWordFilter();

    assertThat(filter.accept("foo"), is(true));
    assertThat(filter.accept("the"), is(false));
    assertThat(filter.accept("a"), is(false));
  }

}
