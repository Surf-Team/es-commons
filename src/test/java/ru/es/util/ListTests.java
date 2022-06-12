package ru.es.util;

import org.junit.jupiter.api.Test;
import ru.es.log.Log;

import java.util.List;

public class ListTests
{
	@Test
	public void testSortByPoints()
	{
		List<String> teams = ListUtils.createList("1", "2", "3");
		int[] points = new int[] { 5, 2, 8};

		for (int i : ListUtils.sortByPoints(teams, points, false))
		{
			Log.warning(i+"");
		}
	}
	@Test
	public void testSortByPoints2()
	{
		List<String> teams = ListUtils.createList("1", "2", "3");
		int[] points = new int[] { 0, 1, 1};
		int[] killPoints = new int[] { 5, 2, 8};


		int[] winnerTeams = ListUtils.sortByPoints(teams, points, false);
		if (points[winnerTeams[0]] == points[winnerTeams[1]] && points[winnerTeams[0]] == points[winnerTeams[2]])
		{
			winnerTeams = ListUtils.sortByPoints(teams, killPoints, false);
		}
		else if (points[winnerTeams[0]] == points[winnerTeams[1]])
		{
			// если счёт первых двух команд одинаковый
			if (killPoints[winnerTeams[1]] > killPoints[winnerTeams[0]])
			{
				int wt0 = winnerTeams[0];
				int wt1 = winnerTeams[1];
				winnerTeams[1] = wt0;
				winnerTeams[0] = wt1;
			}
		}

		for (int place = 0; place < winnerTeams.length; place++)
		{
			Log.warning("place "+place+": "+winnerTeams[place]+", points: "+points[winnerTeams[place]]+", killPoints: "+killPoints[winnerTeams[place]]);
		}
	}
}
