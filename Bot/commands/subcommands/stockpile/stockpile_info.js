const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");
const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        const name = capitalizeFirstLetters(interaction.options.getString("faction-name"));

        axios.get(`http://${serverIP}:${serverPort}/api/faction/get/stockpile/info/${name}`)
            .then(async function(response) {

                const faction = response.data;


                const replyEmbed = new MessageEmbed()
                    .setTitle(`Food-Stockpile of Faction ${name}`)
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
                    .setTitle("Error while getting stockpile information")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.editReply({embeds: [replyEmbed]})
            })

    }
}
