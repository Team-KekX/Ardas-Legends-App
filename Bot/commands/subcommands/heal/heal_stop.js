const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {HEAL} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        const name = interaction.options.getString('army-name')

        const data = {
            executorDiscordId: interaction.member.id,
            armyName: name
        }

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/army/heal-stop", data)
            .then(async function(response) {
                const tokens = response.data.freeTokens;
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Stop healing`)
                    .setColor('GREEN')
                    .setDescription(`${name} has stopped healing. Tokens: ${tokens}/30`)
                    .setThumbnail(HEAL)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while stopping healing")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};