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

        // Name of army or company
        const name= interaction.options.getString("name");

        const data = {
            armyName: name,
        }

        axios.patch(`http://${serverIP}:${serverPort}/api/army/setPaid`, data)
            .then(async function(response) {

                console.log(response)
                console.log(response.data)

                const replyEmbed = new MessageEmbed()
                    .setTitle("Payment received")
                    .setDescription(`${name}'s payment has been received and updated in database`)
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