const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../configs/config.json");
const {UPDATE} = require('../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        //name won't get capitalized here so people have more freedom when naming their chars
        const name = capitalizeFirstLetters(interaction.options.getString('new-name'));

        //data sent to server
        const data = {
            discordId: interaction.member.id,
            charName: name
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/rpchar/name', data)
            .then(async function() {
                //if request successful
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update RpChar Name`)
                    .setColor('GREEN')
                    .setDescription(`The name of your Roleplay Character has been updated to ${name}!`)
                    .setThumbnail(UPDATE)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
            const replyEmbed = new MessageEmbed()
                    .setTitle("Error while updating roleplay character name")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })

    },
};