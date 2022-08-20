const {MessageEmbed} = require('discord.js');
const {CANCEL_MOVE, UPDATE} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");
const {serverIP, serverPort} = require("../../../configs/config.json");
const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        const armyName = capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());

        const data = {
            executorDiscordId: interaction.member.id,
            armyName: armyName
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/movement/cancel-army-move', data)
            .then(async function(response) {
                var currentRegion = response.data.army.currentRegion.id;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Cancel Army Movement`)
                    .setColor('GREEN')
                    .setDescription(`Cancelled the ongoing movement of army '${armyName}'.`)
                    .addFields({name: 'Current Region', value: currentRegion.toString(), inline: false})
                    .setThumbnail(CANCEL_MOVE)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                //error occurred
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to get cancel army movement")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};