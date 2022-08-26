const {SlashCommandBuilder} = require("@discordjs/builders");
const axios = require("axios");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {isMemberStaff} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        const armyName = capitalizeFirstLetters(interaction.options.getString("army-name"));
        const freeTokens = interaction.options.getInteger("tokens");

        const data = {
            armyName: armyName,
            freeTokens: freeTokens,
        }

        axios.patch(`http://${serverIP}:${serverPort}/api/army/set-free-tokens`, data)
            .then(async function(response) {

                const replyEmbed = new MessageEmbed()
                    .setTitle("Free Tokens updated")
                    .setDescription(`${armyName}'s tokens have been updated`)
                    .addFields(
                        {name: 'Free tokens', value: freeTokens.toString(), inline: true},
                        {name: 'Tokens used', value: `${30-freeTokens}/30`, inline: true}
                    )
                    .setColor("GREEN")
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while updating free tokens")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })

    }
}