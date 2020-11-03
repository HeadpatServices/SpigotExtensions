package services.headpat.spigotextensions.brigadier.argumenttypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MaterialArgumentType implements ArgumentType<Material> {
	@Contract(value = " -> new", pure = true)
	public static @NotNull MaterialArgumentType material() {
		return new MaterialArgumentType();
	}

	public static Material getMaterial(@NotNull CommandContext<?> context, String name) {
		return context.getArgument(name, Material.class);
	}

	@Override
	public Material parse(@NotNull StringReader reader) throws CommandSyntaxException {
		String str = reader.readUnquotedString().toUpperCase();
		Material material = Material.getMaterial(str);
		if (material == null) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("invalid material.");
		} else
			return material;

	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList()).forEach(materialName -> {
			if (materialName.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
				builder.suggest(materialName);
		});
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return Arrays.stream(Material.values()).map(Enum::name).collect(Collectors.toList());
	}
}
