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
package handlers.voicedcommandhandlers;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

public class GrandBoss implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"grandboss"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("grandboss"))
		{
			StringBuilder sb = new StringBuilder();
			String file = "data/html/GrandBoss/Status.htm";
			String content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), file);
			NpcHtmlMessage html = new NpcHtmlMessage(content);
			
			for (int boss : Config.GRANDBOSS_LIST)
			{
				L2NpcTemplate npc = NpcData.getInstance().getTemplate(boss);
				String name = npc.getName();
				byte lvl = npc.getLevel();
				long delay = GrandBossManager.getInstance().getStatsSet(boss).getLong("respawn_time");
				
				if (delay <= System.currentTimeMillis())
				{
					sb.append("&nbsp;&nbsp;&nbsp;<font color=\"09596D\">(" + lvl + ")</font> <font color=\"069FCE\">" + name + "</font> <font color=\"9CC300\">is alive</font>" + "<br1>");
				}
				else
				{
					int hours = (int) ((delay - System.currentTimeMillis()) / 1000 / 60 / 60);
					int mins = (int) (((delay - (hours * 60 * 60 * 1000)) - System.currentTimeMillis()) / 1000 / 60);
					int seconds = (int) (((delay - ((hours * 60 * 60 * 1000) + (mins * 60 * 1000))) - System.currentTimeMillis()) / 1000);
					
					sb.append("&nbsp;&nbsp;&nbsp;<font color=\"09596D\">(" + lvl + ")</font> <font color=\"069FCE\">" + name + "</font> <font color=\"C42121\">is dead (" + hours + "h " + mins + "m " + seconds + "s)</font><br1>");
				}
			}
			
			html.replace("%BossStatus%", sb.toString());
			
			activeChar.sendPacket(html);
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
