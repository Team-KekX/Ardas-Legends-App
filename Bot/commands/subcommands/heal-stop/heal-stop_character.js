const {capitalizeFirstLetters, createArmyUnitListString} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {HEAL} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        const data = {
            discordId: interaction.member.id,
        }

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/player/heal-stop", data)
            .then(async function(response) {
                const rpchar = response.data;
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Stop healing`)
                    .setColor('GREEN')
                    .setDescription(`The character ${rpchar.name} has stopped healing.`)
                    .setThumbnail(HEAL)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while stopping healing")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })
    },
};