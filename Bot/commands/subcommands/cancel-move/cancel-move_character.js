const {MessageEmbed} = require('discord.js');
const {CANCEL_MOVE, UPDATE} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");
const {serverIP, serverPort} = require("../../../configs/config.json");

module.exports = {
    async execute(interaction) {

        const data = {
            discordId: interaction.member.id
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/movement/cancel-char-move', data)
            .then(async function(response) {

                console.log(response.data.player.rpChar)

                const name = response.data.player.rpChar.name;
                const currentRegion = response.data.player.rpChar.currentRegion;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Cancel Character Movement`)
                    .setColor('GREEN')
                    .setDescription(`Cancelled the ongoing movement of character ${name}. The character is now at region ${currentRegion}.`)
                    .setThumbnail(CANCEL_MOVE)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Error while cancelling character movement`)
                    .setColor('RED')
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});

            })
    },
};