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

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/player/injure-char", data)
            .then(async function(response) {
                const rpchar = response.data;
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Character injured`)
                    .setColor('GREEN')
                    .setDescription(`${rpchar.name} is now injured.\nThey cannot bind to armies anymore and have possibly been unbound from their last bound army.`)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while injuring character")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })
    },
};