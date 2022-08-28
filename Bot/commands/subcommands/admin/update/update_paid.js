const {SlashCommandBuilder} = require("@discordjs/builders");
const axios = require("axios");
const {serverIP, serverPort} = require("../../../../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {isMemberStaff, capitalizeFirstLetters} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        // Name of army or company
        const name = capitalizeFirstLetters(interaction.options.getString("name"));
        const isPaid = interaction.options.getBoolean("is-paid");

        const data = {
            armyName: name,
            isPaid: isPaid
        }

        axios.patch(`http://${serverIP}:${serverPort}/api/army/setPaid`, data)
            .then(async function(response) {

                const army = response.data;

                const replyEmbed = new MessageEmbed()
                    .setTitle("Payment received")
                    .setDescription(`Payment of army '${name}' has been updated in database`)
                    .setFields(
                        {name: "Army Name", value: name, inline: true},
                        {name: "Faction", value: army.faction.name, inline: true},
                        {name: "Created at", value: army.createdAt.toString(), inline: true},
                        {name: "Is paid", value: capitalizeFirstLetters(army.isPaid.toString()), inline: false},
                    )
                    .setColor("GREEN")
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while updating payment")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })

    }
}