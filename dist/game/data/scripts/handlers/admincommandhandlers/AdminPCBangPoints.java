/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExPCCafePointInfo;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

public class AdminPCBangPoints implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_bang_points",
		"admin_count_bang_points",
		"admin_bangpoints",
		"admin_set_bang_points",
		"admin_subtract_bang_points"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_add_bang_points"))
		{
			try
			{
				if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
				{
					String val = command.substring(22);
					if (!addGamePoints(activeChar, val))
					{
						activeChar.sendMessage("How to use: //add_bang_points Enter numbers");
					}
				}
				else
				{
					activeChar.sendMessage("no target.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of missing parameter
				activeChar.sendMessage("How to use: //add_bang_points Enter numbers");
			}
		}
		else if (command.equals("admin_count_bang_points"))
		{
			if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
			{
				L2PcInstance target = (L2PcInstance) activeChar.getTarget();
				activeChar.sendMessage(target.getName() + "Your total PC bang points are " + target.getPcBangPoints() + "is the point.");
			}
			else
			{
				activeChar.sendMessage("no target.");
			}
		}
		else if (command.equals("admin_bangpoints"))
		{
			openGamePointsMenu(activeChar);
		}
		else if (command.startsWith("admin_set_bang_points"))
		{
			try
			{
				if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
				{
					String val = command.substring(22);
					if (!setPcBangPoints(activeChar, val))
					{
						activeChar.sendMessage("How to use: //set_bang_points Enter numbers");
					}
				}
				else
				{
					activeChar.sendMessage("no target.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of missing parameter
				activeChar.sendMessage("How to use: //set_bang_points Enter numbers");
			}
		}
		else if (command.startsWith("admin_subtract_bang_points"))
		{
			try
			{
				if ((activeChar.getTarget() != null) && activeChar.getTarget().isPlayer())
				{
					String val = command.substring(27);
					if (!subtractGamePoints(activeChar, val))
					{
						activeChar.sendMessage("How to use: //subtract_bang_points Enter numbers");
					}
				}
				else
				{
					activeChar.sendMessage("no target.");
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of missing parameter
				activeChar.sendMessage("How to use: //subtract_bang_points Enter numbers");
			}
		}
		return true;
	}
	
	private void openGamePointsMenu(L2PcInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(activeChar.getHtmlPrefix(), "data/html/admin/pcbang.htm");
		activeChar.sendPacket(html);
	}
	
	private boolean addGamePoints(L2PcInstance admin, String val)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target.isPlayer())
		{
			player = (L2PcInstance) target;
		}
		else
		{
			admin.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		final int points = Integer.valueOf(val);
		if (points < 1)
		{
			admin.sendMessage("input is incorrect.");
			return false;
		}
		
		final int currentPoints = player.getPcBangPoints();
		if (currentPoints < 1)
		{
			player.setPcBangPoints(points);
		}
		else
		{
			player.setPcBangPoints(currentPoints + points);
		}
		
		player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), points, true, true, 1));
		player.sendMessage("GM: " + player.getName() + " Admin give PC Bang Points to me " + points + ".");
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT);
		sm.addLong(points);
		player.sendPacket(sm);
		admin.sendMessage(player.getName() + " PC Bang Points to me " + points + ".");
		admin.sendMessage(player.getName() + " Your total PC Bang Points are " + player.getPcBangPoints() + ".");
		return true;
	}
	
	private boolean setPcBangPoints(L2PcInstance admin, String val)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target.isPlayer())
		{
			player = (L2PcInstance) target;
		}
		else
		{
			admin.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		final int points = Integer.valueOf(val);
		if (points < 0)
		{
			admin.sendMessage("input is incorrect.");
			return false;
		}
		
		player.setPcBangPoints(points);
		player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), points, true, true, 1));
		player.sendMessage("GM: " + player.getName() + " PC Bang Points" + points + " Changed to points.");
		player.sendMessage(" PC bang points " + points + "changed to points..");
		admin.sendMessage(player.getName() + " PC Bang Points " + points + "changed to points.");
		admin.sendMessage(player.getName() + " Your total PC Bang Points are" + points + ".");
		return true;
	}
	
	private boolean subtractGamePoints(L2PcInstance admin, String val)
	{
		L2Object target = admin.getTarget();
		L2PcInstance player = null;
		if (target.isPlayer())
		{
			player = (L2PcInstance) target;
		}
		else
		{
			admin.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		
		final int points = Integer.valueOf(val);
		if (points < 1)
		{
			admin.sendMessage("The input is incorrect.");
			return false;
		}
		
		final int currentPoints = player.getPcBangPoints();
		if (currentPoints <= points)
		{
			player.setPcBangPoints(0);
			player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), currentPoints, false, false, 1));
			player.sendMessage("GM: " + player.getName() + " PC Bang Points " + currentPoints + " Points have been taken");
			player.sendMessage(" PC bang point " + currentPoints + " points have been reduced.");
			admin.sendMessage(player.getName() + " PC Bang Points " + currentPoints + " points have been taken.");
		}
		else
		{
			player.setPcBangPoints(currentPoints - points);
			player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), points, false, false, 1));
			player.sendMessage("GM: " + player.getName() + " PC Bang Points" + points + " points have been taken.");
			player.sendMessage(" PC Bang Points " + points + " points have been reduced.");
			admin.sendMessage(player.getName() + " PC Bang Points " + points + " points have been taken.");
		}
		admin.sendMessage(player.getName() + " Your total PC Bang Points are" + player.getPcBangPoints() + ".");
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}