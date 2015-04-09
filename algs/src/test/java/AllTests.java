import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ com.github.kowshik.bigo.arrays.RearrangeInputToTargetTest.class,
		com.github.kowshik.bigo.binarytree.BstClosestNodeTest.class, com.github.kowshik.bigo.binarytree.BstFindModeTest.class,
		com.github.kowshik.bigo.sorting.MergeSortTest.class, com.github.kowshik.bigo.cache.LRUCacheTest.class,
		com.github.kowshik.bigo.collections.DeepIteratorTest.class, com.github.kowshik.bigo.collections.PeekIteratorTest.class,
		com.github.kowshik.bigo.lists.SinglyLinkedListTest.class, com.github.kowshik.bigo.common.NumberUtilsTest.class,
		com.github.kowshik.bigo.bitsandbytes.NumSetBitsTest.class,
		com.github.kowshik.bigo.arrays.SimplePhoneNumberAllotterTest.class,
		com.github.kowshik.bigo.general.RainWaterTest.class, com.github.kowshik.bigo.sorting.HeapSortTest.class,
		com.github.kowshik.bigo.sorting.QuickSortTest.class, com.github.kowshik.bigo.sorting.StupidSortTest.class,
		com.github.kowshik.bigo.sorting.InsertionSortTest.class, com.github.kowshik.bigo.sorting.SimpleHeapImplTest.class, })
public class AllTests {
	public static void main(String[] args) {
		org.junit.runner.JUnitCore.main("AllTests");
	}
}
