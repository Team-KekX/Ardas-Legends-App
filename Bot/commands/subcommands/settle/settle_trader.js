
module.exports = {
    async execute(interaction) {
        let name=interaction.options.getString('trader-name').toLowerCase();
        let claimbuild=interaction.options.getString('claimbuild-name').toLowerCase();

        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = name.split(" ");
        const arr_claimbuild = claimbuild.split(" ");

        //loop through each element of the array and capitalize the first letter.


        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }
        for (let i = 0; i < arr_claimbuild.length; i++) {
            arr_claimbuild[i] = arr_claimbuild[i].charAt(0).toUpperCase() + arr_claimbuild[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        claimbuild = arr_claimbuild.join(" ");
        await interaction.reply(`${name} has now settled in ${claimbuild}.`);
    },
};