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

import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import ai.npc.AbstractNpcAI;

public class TantaLizardmanSummoner extends AbstractNpcAI
{
	// NPCs
	private static final int TANTA_LIZARDMAN_SUMMONER = 22774;
	private static final int TANTA_LIZARDMAN_SCOUT = 22768;
	private static final int TANTA_GUARD = 18862;
	
	// Skills
	private static final SkillHolder DEMOTIVATION_HEX = new SkillHolder(6425, 1);
	
	public TantaLizardmanSummoner()
	{
		super(TantaLizardmanSummoner.class.getSimpleName(), "ai/zone");
		addAttackId(TANTA_LIZARDMAN_SUMMONER);
		addKillId(TANTA_LIZARDMAN_SUMMONER);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if ((npc.getVariables().getInt("i_ai3", 0) == 0) && (npc.getCurrentHp() <= (npc.getMaxHp() * 0.600000)))
		{
			npc.getVariables().set("i_ai3", 1);
			
			addSkillCastDesire(npc, npc, DEMOTIVATION_HEX.getSkill(), 2147483647);
			addAttackPlayerDesire(addSpawn(TANTA_LIZARDMAN_SCOUT, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false), attacker);
			addAttackPlayerDesire(addSpawn(TANTA_LIZARDMAN_SCOUT, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0, false), attacker);
		}
		
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		// Tanta Guard
		if (getRandom(1000) == 0)
		{
			final L2Attackable guard = (L2Attackable) addSpawn(TANTA_GUARD, npc);
			addAttackPlayerDesire(guard, killer);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new TantaLizardmanSummoner();
	}
}