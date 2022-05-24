/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.zone;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

public class SeerFlouros extends AbstractNpcAI
{
	private static L2Npc SeerFlouros;
	private static L2Npc Follower;
	private static final int duration = 0x493e0;
	private static final int SeerFlourosId = 18559;
	private static final int FollowerId = 18560;
	private static long _LastAttack = 0L;
	private static boolean successDespawn = false;
	private static boolean minion = false;
	
	public SeerFlouros()
	{
		super(SeerFlouros.class.getSimpleName(), "ai/zone");
		
		registerMobs(new int[]
		{
			SeerFlourosId,
			FollowerId
		});
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("despawn"))
		{
			if (!successDespawn && (SeerFlouros != null) && ((_LastAttack + 0x493e0L) < System.currentTimeMillis()))
			{
				cancelQuestTimer("despawn", npc, null);
				SeerFlouros.deleteMe();
				if ((SeerFlouros != null) && (InstanceManager.getInstance().getInstance(SeerFlouros.getInstanceId()) != null))
				{
					InstanceManager.getInstance().getInstance(SeerFlouros.getInstanceId()).setDuration(duration);
				}
				successDespawn = true;
				if (Follower != null)
				{
					Follower.deleteMe();
				}
			}
		}
		else if (event.equalsIgnoreCase("respMinion") && (SeerFlouros != null))
		{
			Follower = addSpawn(FollowerId, SeerFlouros.getX(), SeerFlouros.getY(), SeerFlouros.getZ(), SeerFlouros.getHeading(), false, 0L);
			L2Attackable target = (L2Attackable) SeerFlouros;
			Follower.setRunning();
			((L2Attackable) Follower).addDamageHate(target.getMostHated(), 0, 999);
			Follower.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getId() == SeerFlourosId)
		{
			_LastAttack = System.currentTimeMillis();
			startQuestTimer("despawn", 60000L, npc, null, true);
			SeerFlouros = npc;
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (!minion)
		{
			Follower = addSpawn(FollowerId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0L);
			minion = true;
		}
		_LastAttack = System.currentTimeMillis();
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		switch (npc.getId())
		{
			case SeerFlourosId:
				cancelQuestTimer("despawn", npc, null);
				if (Follower != null)
				{
					Follower.deleteMe();
				}
				break;
			case FollowerId:
				if (SeerFlouros != null)
				{
					startQuestTimer("respMinion", 30000L, npc, null);
				}
				
				break;
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		new SeerFlouros();
	}
}
