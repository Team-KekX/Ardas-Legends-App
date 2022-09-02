const {MessageEmbed} = require('discord.js');
const {STATION_ARMY} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");
const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        executor = interaction.member.id;
        armyName = capitalizeFirstLetters(interaction.options.getString("name"));

        const data = {
            executorDiscordId: executor,
            armyName: armyName
        }

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/army/unstation", data)
            .then(async function(response) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Unstationed from Claimbuild")
                    .setColor("GREEN")
                    .setDescription(`${armyName} has been unstationed`)
                    .setThumbnail(STATION_ARMY)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to unstation")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })


    }
}
