const {SlashCommandBuilder} = require("@discordjs/builders");
const axios = require("axios");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {isMemberStaff, capitalizeFirstLetters} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.editReply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        const name = capitalizeFirstLetters(interaction.options.getString("faction-name"));
        const foodAmount = interaction.options.getInteger("amount-to-remove");

        const data = {
            factionName: name,
            amount: foodAmount
        }
        console.log(data)

        axios.patch(`http://${serverIP}:${serverPort}/api/faction/update/stockpile/remove`, data)
            .then(async function(response) {

                const faction = response.data;

                const replyEmbed = new MessageEmbed()
                    .setTitle("Stockpile updated")
                    .setDescription(`Food stockpile of ${name} has been updated to ${faction.amount} Stacks! Removed ${foodAmount}`)
                    .setFields(
                        {name: "Faction", value: name, inline: true},
                        {name: "Stockpile", value: faction.amount.toString(), inline: true},
                    )
                    .setColor("GREEN")
                    .setTimestamp()

                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while removing from food stockpile")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.editReply({embeds: [replyEmbed]})
            })

    }
}
