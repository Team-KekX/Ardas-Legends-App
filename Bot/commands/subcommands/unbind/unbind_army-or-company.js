const {MessageEmbed} = require('discord.js');
const {UNBIND} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");
const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        target = interaction.options.getUser("target-player");

        const data = {
            executorDiscordId: interaction.member.id,
            targetDiscordId: target.id,
            armyName: capitalizeFirstLetters(interaction.options.getString("army-or-company-name"))
        }

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/army/unbind", data)
            .then(async function(response) {

                const armyName = response.data.name;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Unbind army`)
                    .setColor('GREEN')
                    .setDescription(`${target} has been unbound from the army ${armyName}.`)
                    .setThumbnail(UNBIND)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to unbind!")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};