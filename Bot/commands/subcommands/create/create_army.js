const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {CREATE} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const units = interaction.options.getString('units');


        const data = {
            executorDiscordId: interaction.member.id,
            name: name,
            armyType: 'ARMY',
            claimBuildName: claimbuild,
            unitString: units
        }

        axios.post(`http://${serverIP}:${serverPort}/api/army/create`, data)
            .then(async function(response) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Army created!")
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error) {
                const replyEmbed =new MessageEmbed()
                    .setTitle("Error while creating army")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};
