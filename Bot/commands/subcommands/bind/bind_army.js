const {MessageEmbed} = require('discord.js');
const {BIND} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");
const {serverIP, serverPort} = require("../../../configs/config.json");
const http = require("http");

module.exports = {
    async execute(interaction) {
        const data = {
            executorDiscordId: interaction.options.member.id,
            targetDiscordId: interaction.options.getUser('target').id,
            armyName: interaction.options.getString('army-name')
        }

        axios.patch( 'http://' + serverIP + serverPort + 'api/army/bind', data)
            .then(async function(response) {

                const player = response.data.boundTo.ign;
                const armyName = response.data.name;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Bind Character to Army`)
                    .setColor('GREEN')
                    .setDescription(`Bound player "${player}" to army "${armyName}"`)
                    .setThumbnail(BIND)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
            })
    },
};