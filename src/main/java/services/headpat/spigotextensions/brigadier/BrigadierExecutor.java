package services.headpat.spigotextensions.brigadier;

import com.google.common.collect.ObjectArrays;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Creates autocomplete suggestions and onCommand handling automatically using {@link LiteralArgumentBuilder}.
 */
public class BrigadierExecutor implements TabExecutor {
	/**
	 * Returning 1 from an executor in the LiteralArgumentBuilder will act as true and 0 and below will act as false.
	 *
	 * @param argumentBuilder The literal argument builder to create the command from.
	 */
	public BrigadierExecutor(@NotNull Supplier<LiteralArgumentBuilder<CommandSender>> argumentBuilder) {
		Brigadier.getCommandDispatcher().register(argumentBuilder.get());
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		try {
			int result = Brigadier.getCommandDispatcher().execute(getCommandString(alias, args), sender);
			if (result <= 0) {
				sendUsageMessage(sender, alias, Brigadier.getCommandDispatcher());
				return true;
			}
		} catch (CommandSyntaxException e) {
			if (e.getMessage() != null)
				sender.sendMessage(ChatColor.RED + e.getMessage());

			sendUsageMessage(sender, alias, Brigadier.getCommandDispatcher());
			return true;
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		String commandString = getCommandString(alias, args);
		Suggestions suggestions = Brigadier.getCommandDispatcher().getCompletionSuggestions(Brigadier.getCommandDispatcher().parse(commandString, sender)).join();
		return suggestions.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
	}

	private static void sendUsageMessage(@NotNull CommandSender sender, String alias, @NotNull CommandDispatcher<CommandSender> commandDispatcher) {
		sender.sendMessage(ChatColor.RED + "Usages:");
		for (String s : commandDispatcher.getAllUsage(commandDispatcher.getRoot().getChild(alias), sender, true))
			sender.sendMessage(ChatColor.RED + "/" + s);
	}

	public String getCommandString(String alias, String[] args) {
		return String.join(" ", ObjectArrays.concat(alias, args));
	}
}
