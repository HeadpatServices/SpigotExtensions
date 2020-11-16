package services.headpat.spigotextensions.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import org.bukkit.command.CommandSender;

public class Brigadier {
	@Getter
	private static CommandDispatcher<CommandSender> commandDispatcher;
}
