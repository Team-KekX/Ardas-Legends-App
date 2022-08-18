const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {UNBIND} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        target = interaction.options.getUser("target");

        const data = {
            executorDiscordId: interaction.member.id,
            targetDiscordId: target.id,
            armyName: interaction.options.getString("army-name")
        }

        axios.patch("http://" + serverIP + ":" + serverPort + "/api/army/unbind-army", data)
            .then(async function(response) {

                const armyName = response.data.name;

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Unbind army`)
                    .setColor('RED')
                    .setDescription(`${target} has been unbound from the army ${armyName}.`)
                    .setThumbnail(UNBIND)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while trying to unbind!")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};