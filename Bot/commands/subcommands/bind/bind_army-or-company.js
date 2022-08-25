const {MessageEmbed} = require('discord.js');
const {BIND} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");
const {serverIP, serverPort} = require("../../../configs/config.json");
const http = require("http");
const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        target = interaction.options.getUser("target-player");

        const data = {
            executorDiscordId: interaction.member.id,
            targetDiscordId: target.id,
            armyName: capitalizeFirstLetters(interaction.options.getString('army-or-company-name'))
        }

        axios.patch( 'http://' + serverIP + ':' + serverPort + '/api/army/bind', data)
            .then(async function(response) {

                const armyName = response.data.name;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Bind Character to Army`)
                    .setColor('GREEN')
                    .setDescription(`Bound player "${target}" to army "${armyName}"`)
                    .setThumbnail(BIND)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error when trying to bind")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
    },
};