const {MessageEmbed} = require("discord.js");
const {interactionInAllowedChannel, wrongChannelReply} = require("../utils/utilities");
const log = require("log4js").getLogger();
module.exports = {
    name: 'interactionCreate',
    async execute(interaction) {
        log.trace(`${interaction.user.tag} triggered an interaction in channel #${interaction.channel.name}`)
        if (!interaction.isCommand()) return;
        const command = interaction.client.commands.get(interaction.commandName);
        log.info(`${interaction.user.tag} used command ${interaction.commandName} in channel #${interaction.channel.name}`)

        if (!command) return;

        if (!interactionInAllowedChannel(interaction)) {
            await wrongChannelReply(interaction)
        } else {
            try {
                log.trace("Starting deferReply")
                await interaction.deferReply();
                log.trace("deferReply done")
                log.debug("Executing command")
                command.execute(interaction);
            } catch (error) {
                log.error(`Encountered unexpected error: ${error.message} - Stacktrace: ${error.stack}`)
                const replyEmbed = new MessageEmbed()
                    .setTitle("An unexpected error occured")
                    .setColor("RED")
                    .setDescription(error.toString() + "\nPlease contact the devs")
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            }
        }
    }
};