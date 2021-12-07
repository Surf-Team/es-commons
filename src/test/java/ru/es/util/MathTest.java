package ru.es.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.es.log.Log;
import ru.es.math.ESMath;

public class MathTest
{
	@Test
	public void testSlice()
	{
		testSlice(10, 3);
		testSlice(11, 3);
		testSlice(11, 4);
		testSlice(1, 3);
		testSlice(2, 3);
		testSlice(3, 3);
		testSlice(3, 1);
	}

	private void testSlice(int fullSize, int newCount)
	{
		int slice[] = ESMath.slice(fullSize, newCount);

		int test = 0;
		StringBuilder ret = new StringBuilder();
		ret.append("Slice "+fullSize+" to "+newCount+", result: ");
		for (int peace : slice)
		{
			test += peace;
			ret.append(peace);
			ret.append(" ");
		}
		Log.warning(ret.toString());
		Assertions.assertEquals(test, fullSize);
	}
}
