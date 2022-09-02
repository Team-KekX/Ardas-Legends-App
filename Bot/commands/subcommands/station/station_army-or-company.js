const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {STATION_ARMY} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        name = capitalizeFirstLetters(interaction.options.getString("name"));
        claimbuild = capitalizeFirstLetters(interaction.options.getString("claimbuild-name"));

        const data = {
            executorDiscordId: interaction.member.id,
            armyName: name,
            claimbuildName: claimbuild
        }

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/army/station", data)
            .then(async function(response) {
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Stationed! `)
                    .setColor('GREEN')
                    .setDescription(`${name} is now stationed at ${claimbuild}.`)
                    .setThumbnail(STATION_ARMY)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to station army")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })

    },
};