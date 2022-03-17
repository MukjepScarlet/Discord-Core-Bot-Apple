package com.demod.dcba;

import java.util.Optional;
import java.util.function.Function;

import com.demod.dcba.CommandDefinition.CommandRestriction;
import com.demod.dcba.CommandHandler.SimpleEmbedResponse;
import com.demod.dcba.CommandHandler.SimpleEmbedSlashResponse;
import com.demod.dcba.CommandHandler.SimpleResponse;
import com.demod.dcba.CommandHandler.SimpleSlashResponse;
import com.demod.dcba.CommandHandler.SlashCommandHandler;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public final class DCBA {

	public static interface Builder {
		CommandBuilder addCommand(String command, CommandHandler handler);

		default CommandBuilder addCommand(String command, SimpleEmbedResponse handler) {
			return addCommand(command, (CommandHandler) handler);
		}

		default CommandBuilder addCommand(String command, SimpleResponse handler) {
			return addCommand(command, (CommandHandler) handler);
		}

		Builder addReactionWatcher(ReactionWatcher watcher);

		default Builder addReactionWatcher(ReactionWatcher.SimpleWatcher watcher) {
			return addReactionWatcher((ReactionWatcher) watcher);
		}

		default CommandBuilder addSlashCommand(String command, SimpleEmbedSlashResponse handler) {
			return addCommand(command, (CommandHandler) handler).slashCommandOnly();
		}

		default CommandBuilder addSlashCommand(String command, SimpleSlashResponse handler) {
			return addCommand(command, (CommandHandler) handler).slashCommandOnly();
		}

		default CommandBuilder addSlashCommand(String command, SlashCommandHandler handler) {
			return addCommand(command, handler).slashCommandOnly();
		}

		Builder addTextWatcher(TextWatcher watcher);

		default Builder addTextWatcher(TextWatcher.SimpleWatcher watcher) {
			return addTextWatcher((TextWatcher) watcher);
		}

		DiscordBot create();

		Builder ignorePrivateChannels();

		InfoBuilder setInfo(String botName);

		Builder withCommandPrefix(String commandPrefix);

		Builder withCustomSetup(Function<JDABuilder, JDABuilder> customSetup);

		Builder withExceptionHandler(ExceptionHandler handler);
	}

	private static class BuilderImpl implements Builder, CommandBuilder, InfoBuilder {
		DiscordBot bot = new DiscordBot();

		CommandDefinition command = null;

		private BuilderImpl() {
		}

		@Override
		public CommandBuilder addCommand(String name, CommandHandler handler) {
			if (command != null) {
				bot.addCommand(command);
				command = null;
			}
			command = new CommandDefinition(name, handler);
			return this;
		}

		@Override
		public Builder addReactionWatcher(ReactionWatcher watcher) {
			bot.setReactionWatcher(Optional.of(watcher));
			return this;
		}

		@Override
		public Builder addTextWatcher(TextWatcher watcher) {
			bot.setTextWatcher(Optional.of(watcher));
			return this;
		}

		@Override
		public CommandBuilder adminOnly() {
			command.setRestriction(CommandRestriction.ADMIN_ONLY);
			return this;
		}

		@Override
		public DiscordBot create() {
			if (command != null) {
				bot.addCommand(command);
				command = null;
			}
			bot.initialize();
			return bot;
		}

		@Override
		public CommandBuilder guildChannelOnly() {
			command.setRestriction(CommandRestriction.GUILD_CHANNEL_ONLY);
			command.clearRestriction(CommandRestriction.PRIVATE_CHANNEL_ONLY);
			return this;
		}

		@Override
		public Builder ignorePrivateChannels() {
			bot.setIgnorePrivateChannels(true);
			return this;
		}

		@Override
		public CommandBuilder messageCommandOnly() {
			command.setRestriction(CommandRestriction.MESSAGE_COMMANDS_ONLY);
			command.clearRestriction(CommandRestriction.SLASH_COMMANDS_ONLY);
			return this;
		}

		@Override
		public CommandBuilder privateChannelOnly() {
			command.setRestriction(CommandRestriction.PRIVATE_CHANNEL_ONLY);
			command.clearRestriction(CommandRestriction.GUILD_CHANNEL_ONLY);
			return this;
		}

		@Override
		public InfoBuilder setInfo(String botName) {
			bot.getInfo().setBotName(Optional.of(botName));
			return this;
		}

		@Override
		public CommandBuilder slashCommandOnly() {
			command.setRestriction(CommandRestriction.SLASH_COMMANDS_ONLY);
			command.clearRestriction(CommandRestriction.MESSAGE_COMMANDS_ONLY);
			return this;
		}

		@Override
		public CommandBuilder withAliases(String... aliases) {
			command.setAliases(Optional.of(aliases));
			return this;
		}

		@Override
		public Builder withCommandPrefix(String commandPrefix) {
			bot.setCommandPrefix(Optional.of(commandPrefix));
			return this;
		}

		@Override
		public InfoBuilder withCredits(String group, String... names) {
			bot.getInfo().addCredits(group, names);
			return this;
		}

		@Override
		public Builder withCustomSetup(Function<JDABuilder, JDABuilder> customSetup) {
			bot.setCustomSetup(customSetup);
			return this;
		}

		@Override
		public Builder withExceptionHandler(ExceptionHandler handler) {
			bot.setExceptionHandler(handler);
			return this;
		}

		@Override
		public CommandBuilder withHelp(String description) {
			command.setHelp(Optional.of(description));
			return this;
		}

		@Override
		public InfoBuilder withInvite(Permission... permissions) {
			bot.getInfo().setAllowInvite(true);
			bot.getInfo().setInvitePermissions(permissions);
			return this;
		}

		@Override
		public CommandBuilder withOptionalParam(OptionType type, String name, String description) {
			command.addOption(new CommandOptionDefinition(type, name, description, false));
			return this;
		}

		@Override
		public CommandBuilder withParam(OptionType type, String name, String description) {
			command.addOption(new CommandOptionDefinition(type, name, description, true));
			return this;
		}

		@Override
		public InfoBuilder withSupport(String supportMessage) {
			bot.getInfo().setSupport(Optional.of(supportMessage));
			return this;
		}

		@Override
		public InfoBuilder withTechnology(String name, String description) {
			bot.getInfo().addTechnology(name, Optional.empty(), description);
			return this;
		}

		@Override
		public InfoBuilder withTechnology(String name, String version, String description) {
			bot.getInfo().addTechnology(name, Optional.of(version), description);
			return this;
		}

		@Override
		public InfoBuilder withVersion(String version) {
			bot.getInfo().setVersion(Optional.of(version));
			return this;
		}
	}

	public static interface CommandBuilder extends Builder {
		CommandBuilder adminOnly();

		CommandBuilder guildChannelOnly();

		CommandBuilder messageCommandOnly();

		CommandBuilder privateChannelOnly();

		CommandBuilder slashCommandOnly();

		CommandBuilder withAliases(String... aliases);

		CommandBuilder withHelp(String description);

		CommandBuilder withOptionalParam(OptionType type, String name, String description);

		CommandBuilder withParam(OptionType type, String name, String description);
	}

	public static interface InfoBuilder extends Builder {
		InfoBuilder withCredits(String group, String... names);

		InfoBuilder withInvite(Permission... permissions);

		InfoBuilder withSupport(String supportMessage);

		InfoBuilder withTechnology(String name, String description);

		InfoBuilder withTechnology(String name, String version, String description);

		InfoBuilder withVersion(String version);
	}

	public static Builder builder() {
		return new BuilderImpl();
	}

}
