package ru.es.util;

import org.junit.jupiter.api.Test;
import ru.es.log.Log;
import ru.es.math.ESMath;

import java.util.ArrayList;
import java.util.List;

public class TestCapacityMethod
{
	@Test
	public void testSlice()
	{
		List<Arena> arenaList = new ArrayList<>();
		arenaList.add(new Arena(40, "Elven Village"));
		arenaList.add(new Arena(40, "Crypts of Disgrace"));
		arenaList.add(new Arena(50, "Heine"));
		arenaList.add(new Arena(70, "Giran"));

		int sum = arenaList.stream().mapToInt(e->e.maxPlayers).sum();

		//Log.warning("К примеру, есть арены по 40, 60, 80, 100 игроков");
		//Log.warning("Значит, максимальное количество игроков при регистрации будет: "+sum);
		List<Object> players = new ArrayList<>();
		for (int i = 0; i <= sum; i+=1)
		{
			List<Arena> arenasSelected = ESMath.selectFitCapacity(arenaList, i,  3);
			//Log.warning("arenasSelected: "+arenasSelected.size());
			int sum2 = arenasSelected.stream().mapToInt(e->e.maxPlayers).sum() * 3;

			try
			{
				var splitMap = ESMath.distribute(players, arenasSelected, 3);
				List<String> splitMapString = new ArrayList<>();
				for (var e : splitMap.entrySet())
				{
					splitMapString.add(e.getKey()+"->"+e.getValue().size());
				}

				Log.warning("If registered "+i+": then we select arenas: "+ListUtils.toString(arenasSelected, ", ")+". Free space: "+(sum2-i)+". " +
						"Players split to arenas: "+ListUtils.toString(splitMapString, ", "));

				players.add("Player"+i);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}


	class Arena implements Capacity
	{
		final int maxPlayers;
		private final String name;

		Arena(int maxPlayers, String name) {this.maxPlayers = maxPlayers;
			this.name = name;
		}

		@Override
		public int capacity()
		{
			return maxPlayers;
		}

		@Override
		public String toString()
		{
			return maxPlayers+"-size-"+name;
		}
	}
}
