package com.github.kowshik.bigo.lists;

import static org.junit.Assert.assertEquals;
import com.github.kowshik.bigo.linkedlists.SinglyLinkedList;

import org.junit.Before;
import org.junit.Test;

public class SinglyLinkedListTest {

	private SinglyLinkedList<Integer> tested;

	@Before
	public void setup() {
		tested = new SinglyLinkedList<Integer>();
	}

	@Test
	public void reverseEmptyList() {
		tested.reverse();
		assertEquals(0, tested.size());
	}
}
