const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const {DISBAND} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        const name = capitalizeFirstLetters(interaction.options.getString('name'));
        const executor = interaction.member.id;

        const data = {
            executorDiscordId: executor,
            armyName: name
        }

        axios.delete(`http://${serverIP}:${serverPort}/api/army/disband`, {data: data})
            .then(async function(response) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Disbanded")
                    .setColor("GREEN")
                    .setDescription(`${name} has been disbanded!`)
                    .setThumbnail(DISBAND)
                    .setTimestamp()

                await interaction.editReply({embeds: [replyEmbed]})
            })
            .catch(async function(error)  {
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Error while trying to disband ${name}`)
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setThumbnail(DISBAND)
                    .setTimestamp()

                await interaction.editReply({embeds: [replyEmbed]})
            })
    },
};