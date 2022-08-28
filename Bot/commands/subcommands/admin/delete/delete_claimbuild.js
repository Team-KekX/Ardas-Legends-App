const {capitalizeFirstLetters} = require("../../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");
const {isMemberStaff} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        const name = capitalizeFirstLetters(interaction.options.getString('name'));

        const data = {
            claimbuildName: name
        }

        axios.delete(`http://${serverIP}:${serverPort}/api/claimbuild/delete`, {data: data})
            .then(async function(response) {
                console.log(response.data)

                unstationedArmies = response.data.unstationedArmies.join("\n")
                deletedArmies= response.data.deletedArmies.join("\n")

                if(unstationedArmies === "")
                    unstationedArmies = "None"

                if(deletedArmies === "")
                    deletedArmies = "None"

                const replyEmbed = new MessageEmbed()
                    .setTitle("Staff-Deleted Claimbuild")
                    .setColor("GREEN")
                    .setDescription(`${name} has been deleted!`)
                    .addFields([
                        {name: "Unstationed Armies/Companies", value: unstationedArmies, inline:false},
                        {name: "Deleted Armies/Companies", value: deletedArmies, inline:false}
                    ])
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error)  {
                console.log(error)
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Error while trying to delete claimbuild: ${name}`)
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};
