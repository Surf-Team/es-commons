package ru.es.lang.logic;

import ru.es.lang.ESValueDefault;
import ru.es.math.Rnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateSequence
{
	private final Map<Integer, ESValueDefault<Integer>> countForStates = new HashMap<>();
	private Integer currentState = null;

	public int getNextState(int maxCount, List<Integer> availableQuestStates)
	{
		if (currentState == null)
			currentState = Rnd.getRndFromCollection(availableQuestStates);

		ESValueDefault<Integer> countForState = countForStates.get(currentState);
		if (countForState == null)
		{
			countForState = new ESValueDefault<>(0);
			countForStates.put(currentState, countForState);
		}

		if (countForState.get() > maxCount || !availableQuestStates.contains(currentState))
		{
			List<Integer> availableNextStates = new ArrayList<>(availableQuestStates);
			availableNextStates.remove(currentState);
			currentState = Rnd.getRndFromCollection(availableNextStates);

			// сбрасываем все на 0
			for (int state : countForStates.keySet())
			{
				countForStates.get(state).set(0);
			}
		}
		else
			countForState.set(countForState.get()+1);


		return currentState;
	}

	public void setCurrentState(Integer state)
	{
		this.currentState = state;
	}

	public Integer getCurrentState()
	{
		return currentState;
	}

	public void cleanCounters()
	{
		countForStates.clear();
	}
}
