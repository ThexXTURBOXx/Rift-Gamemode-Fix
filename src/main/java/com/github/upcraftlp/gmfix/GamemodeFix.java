package com.github.upcraftlp.gmfix;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.impl.GameModeCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.rift.listener.CommandAdder;
import org.dimdev.rift.listener.MinecraftStartListener;

@SuppressWarnings("unused")
public class GamemodeFix implements MinecraftStartListener, CommandAdder {

	private static final Logger log = LogManager.getLogger("GamemodeFix");

	public static final String MODID = "gm_fix";

	public static Logger getLog() {
		return log;
	}

	@Override
	public void onMinecraftStart() {
		log.info("Loaded Gamemode Fix!");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
		ArgumentBuilder<CommandSource, ?> builder = dispatcher.getRoot().getChild("gamemode").createBuilder(); //get the /gamemode command node
		for (GameType type : GameType.values()) {
			if (type != GameType.NOT_SET) { //we don't want to be able to set the NOT_SET gamemode!
				builder
						.then(Commands.literal(String.valueOf(type.ordinal() - 1)).executes(context -> GameModeCommand.setGameMode(context, Collections.singleton(assertIsPlayer(context.getSource())), type))
								.then(Commands.argument("target", EntityArgument.players()).executes(source -> GameModeCommand.setGameMode(source, EntityArgument.getPlayers(source, "target"), type))));
			}
		}
		dispatcher.register((LiteralArgumentBuilder<CommandSource>) builder);
	}

	public EntityPlayerMP assertIsPlayer(CommandSource source) throws CommandSyntaxException {
		Entity e = source.assertIsEntity();
		if (!(e instanceof EntityPlayerMP))
			throw CommandSource.REQUIRES_ENTITY_EXCEPTION_TYPE.create();
		return (EntityPlayerMP) e;
	}

}
