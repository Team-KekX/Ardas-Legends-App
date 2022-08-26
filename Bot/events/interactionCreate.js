const {MessageEmbed} = require("discord.js");
module.exports = {
    name: 'interactionCreate',
    async execute(interaction) {
        console.log(`${interaction.user.tag} in #${interaction.channel.name} triggered an interaction.`);
        if (!interaction.isCommand()) return;
        const command = interaction.client.commands.get(interaction.commandName);

        if (!command) return;

        try {

            await command.execute(interaction);
        } catch (error) {
            console.log(error)
            const replyEmbed = new MessageEmbed()
                .setTitle("An unexpected error occured")
                .setColor("RED")
                .setDescription(error.toString() + "\nPlease contact the devs")
                .setTimestamp()
            await interaction.reply({embeds: [replyEmbed]})
        }
    },
};